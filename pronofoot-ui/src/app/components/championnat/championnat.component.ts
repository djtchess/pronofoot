// src/app/components/championnat/championnat.component.ts
import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { AsyncPipe, DatePipe, NgIf } from '@angular/common';
import { take, switchMap, BehaviorSubject, Observable, finalize, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';   


import { ChampionnatService } from '../../services/championnat.service';
import { MatchService } from '../../services/match.service';
import { Championnat } from '../../models/championnat.model';
import { ClassementComponent } from '../classement/classement.component';

@Component({
  standalone: true,
  selector: 'app-championnat',
  templateUrl: './championnat.component.html',
  styleUrls: ['./championnat.component.scss'],
  imports: [NgIf, AsyncPipe, RouterLink, RouterOutlet, ClassementComponent          ]
})
export class ChampionnatComponent implements OnInit {
  /** Flux du championnat (mise à jour via syncTrigger) */
  championnat$!: Observable<Championnat>;

  seasonId: string | null = null;


  /** Déclencheur interne : `true` ⇒ forcer la synchro back-end puis reload */
  private readonly syncTrigger = new BehaviorSubject<boolean>(false);

  /** Code du championnat dans l’URL (`:code`) */
  code = '';

  /*— DI par inject() pour alléger le constructeur —*/
  private readonly route = inject(ActivatedRoute);
  private readonly championnatService = inject(ChampionnatService);
  private readonly matchService = inject(MatchService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  /* ------------------------------------------------------------------ */
  /* cycle de vie                                                       */
  /* ------------------------------------------------------------------ */
 ngOnInit(): void {

    /* Écoute du changement du paramètre :code                            */
    this.route.params
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(params => {

        this.code = params['code'];
        this.seasonId = this.route.snapshot.paramMap.get('id') ?? '';



        /* (Ré)initialise le flux championnat$                            */
        this.championnat$ = this.syncTrigger.pipe(
          switchMap(sync =>
            this.championnatService.getChampionnat(this.code, sync)
          )
        );

        /* Premier chargement (sans resynchronisation)                    */
        this.syncTrigger.next(false);

        /* Si l’URL n’a pas encore `saisons/:id`, on y redirige            */
        this.championnat$
          .pipe(take(1), takeUntilDestroyed(this.destroyRef))
          .subscribe(champ => {
            const hasId = this.route.snapshot.paramMap.has('id');
            if (!hasId && champ.currentSeason) {
              this.router.navigate(
                ['/championnats', this.code,
                 'saisons', champ.currentSeason.year],
                { replaceUrl: true }
              );
            }
          });
      });
  }

  /* ------------------------------------------------------------------ */
  /* méthodes publiques (visibles dans le template)                      */
  /* ------------------------------------------------------------------ */

/** Bouton « Synchroniser avec l’API »                                 */
  onSync(): void {
    this.championnatService.getChampionnat(this.code, /* sync */ true).pipe(
      take(1),
      /* Quand le championnat est synchronisé, on importe les matches     */
      switchMap(champ =>
        this.matchService.importMatches(
          this.code,
          champ.currentSeason?.year.toString() ?? ''
        )
      ),
      tap(() => this.reloadChampionnat(true))
    ).subscribe({
      error: err => console.error('Synchronisation KO :', err)
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
