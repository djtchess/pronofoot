// src/app/components/championnat/championnat.component.ts
import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { AsyncPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  take,
  switchMap,
  map,                               // ← NEW
  tap,
  BehaviorSubject,
  Observable,
  forkJoin
} from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ChampionnatService } from '../../services/championnat.service';
import { MatchService } from '../../services/match.service';
import { Championnat } from '../../models/championnat.model';
import { Saison } from '../../models/saison.model';
import { ClassementComponent } from '../classement/classement.component';

@Component({
  standalone: true,
  selector: 'app-championnat',
  templateUrl: './championnat.component.html',
  styleUrls: ['./championnat.component.scss'],
  imports: [
    NgIf,
    NgForOf,
    AsyncPipe,
    RouterLink,
    RouterOutlet,
    FormsModule,
    ClassementComponent
  ]
})
export class ChampionnatComponent implements OnInit {

  /* ---------- flux principal (| async dans le template) ---------- */
  championnat$!: Observable<Championnat>;

  /* ---------- copie locale (si besoin hors template) ------------- */
  private championnat!: Championnat;

  /* ---------- UI « saisons » ------------------------------------- */
  saisons: Saison[] = [];
  selectedSeason!: Saison;

  /* ---------- route params & triggers ---------------------------- */
  private seasonId!: string;
  private readonly syncTrigger = new BehaviorSubject<boolean>(false);
  code = '';

  /* ---------- injections ----------------------------------------- */
  private readonly route            = inject(ActivatedRoute);
  private readonly router           = inject(Router);
  private readonly destroyRef       = inject(DestroyRef);
  private readonly championnatSrv   = inject(ChampionnatService);
  private readonly matchSrv         = inject(MatchService);

  /* =============================================================== */
  ngOnInit(): void {

    /* écoute des paramètres :code & :id                             */
    this.route.params
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(params => {

        this.code     = params['code'];
        this.seasonId = params['id'];

        /* ---- (re)crée le flux championnat$ ---------------------- */
        this.championnat$ = this.syncTrigger.pipe(
          switchMap(sync =>
            forkJoin({
              champ:   this.championnatSrv.getChampionnat(this.code, this.seasonId!, sync),
              saisons: this.championnatSrv.getSaisons(this.code)
            })
          ),
          tap(({ champ, saisons }) => {
            /* copie locale + liste triée décroissante */
            this.championnat = champ;
            this.saisons     = [...saisons].sort((a, b) => b.year - a.year);
            

            /* saison sélectionnée : URL -> currentSeason -> première */
            const fromUrl = this.seasonId ? this.saisons.find(s => s.id === +this.seasonId!) : undefined;
            this.selectedSeason = fromUrl ?? champ.currentSeason ?? this.saisons[0];
          }),
          /* on ne laisse sortir que le Championnat */
          map(({ champ }) => champ)
        );

        /* premier chargement (sans resynchronisation)              */
        this.syncTrigger.next(false);

        /* redirection si aucun :id dans l’URL                       */
        if (!this.seasonId) {
          this.championnat$
            .pipe(take(1), takeUntilDestroyed(this.destroyRef))
            .subscribe(champ => {
              if (champ.currentSeason) {
                this.router.navigate(
                  ['/championnats', this.code, 'saisons', champ.currentSeason.year],
                  { replaceUrl: true }
                );
              }
            });
        }
      });
  }

  /* ----------------- UI callbacks -------------------------------- */

  /** Changement dans la liste déroulante */
  onSeasonChange(): void {
    this.router.navigate(
      ['/championnats', this.code, 'saisons', this.selectedSeason.id],
      { replaceUrl: true }
    );
  }

  /** Bouton « Synchroniser » */
  onSync(): void {
    this.championnatSrv.getChampionnat(this.code, this.seasonId, true).pipe(
      take(1),
      switchMap(champ =>
        this.matchSrv.importMatches(
          this.code,
          this.selectedSeason.year.toString() ?? ''
        )
      ),
      tap(() => this.reloadChampionnat(false))
    ).subscribe({
      error: err => console.error('Synchronisation KO :', err)
    });
  }

  /* ----------------- helpers privés ------------------------------ */
  private reloadChampionnat(sync: boolean): void {
    this.syncTrigger.next(sync);
  }
}
