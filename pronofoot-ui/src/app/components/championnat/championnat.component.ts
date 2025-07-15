// src/app/components/championnat/championnat.component.ts
import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterOutlet } from '@angular/router';
import { AsyncPipe, NgIf } from '@angular/common';
import { take, switchMap, BehaviorSubject, Observable } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';   


import { ChampionnatService } from '../../services/championnat.service';
import { MatchService } from '../../services/match.service';
import { Championnat } from '../../models/championnat.model';

@Component({
  standalone: true,
  selector: 'app-championnat',
  templateUrl: './championnat.component.html',
  styleUrls: ['./championnat.component.scss'],
  imports: [NgIf, AsyncPipe, RouterLink, RouterOutlet]
})
export class ChampionnatComponent implements OnInit {
  /** Flux du championnat (mise à jour via syncTrigger) */
  championnat$!: Observable<Championnat>;

  /** Déclencheur interne : `true` ⇒ forcer la synchro back-end puis reload */
  private readonly syncTrigger = new BehaviorSubject<boolean>(false);

  /** Code du championnat dans l’URL (`:code`) */
  private code = '';

  /*— DI par inject() pour alléger le constructeur —*/
  private readonly route = inject(ActivatedRoute);
  private readonly championnatService = inject(ChampionnatService);
  private readonly matchService = inject(MatchService);
  private readonly destroyRef = inject(DestroyRef);

  /* ------------------------------------------------------------------ */
  /* cycle de vie                                                       */
  /* ------------------------------------------------------------------ */
  ngOnInit(): void {
    /* écoute du param :code */
    this.route.params
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(params => {
        this.code = params['code'];

        /* initialisation (une seule fois) du flux championnat$ */
        this.championnat$ = this.syncTrigger.pipe(
          switchMap(sync =>
            this.championnatService.getChampionnat(this.code, sync)
          )
        );

        /* premier chargement (sans forcer la synchro back) */
        this.syncTrigger.next(false);
      });
  }

  /* ------------------------------------------------------------------ */
  /* méthodes publiques (visibles dans le template)                      */
  /* ------------------------------------------------------------------ */

  /** Bouton « Synchroniser avec l’API » */
  onSync(): void {
    /* 1) récupérer la saison courante pour l’endpoint d’import */
    this.championnat$
      .pipe(take(1), takeUntilDestroyed(this.destroyRef))
      .subscribe(champ => {
        const saison = champ.currentSeason.year.toString();

        /* 2) appel back-end pour importer tous les matches */
        this.matchService.importMatches(this.code, saison).subscribe({
          next: () => this.reloadChampionnat(true),   // 3) rafraîchit l’affichage
          error: err =>
            console.error('Erreur lors de l’import des matchs :', err)
        });
      });
  }

  /* ------------------------------------------------------------------ */
  /* helpers privés                                                     */
  /* ------------------------------------------------------------------ */

  /** Force le rechargement du championnat ; si `sync=true`, le back est interrogé */
  private reloadChampionnat(sync: boolean): void {
    this.syncTrigger.next(sync);
  }
}
