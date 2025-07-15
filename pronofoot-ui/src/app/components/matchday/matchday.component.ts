// src/app/components/matchday/matchday.component.ts
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AsyncPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { MatchService} from '../../services/match.service';
import {
  BehaviorSubject,
  map,
  Observable,
  switchMap,
  take
} from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Match } from '../../models/match.model';

@Component({
  standalone: true,
  selector: 'app-matchday',
  templateUrl: './matchday.component.html',
  styleUrls: ['./matchday.component.scss'],
  imports: [NgIf, NgFor, AsyncPipe, DatePipe, FormsModule]
})
export class MatchdayComponent implements OnInit {
  // liste des journées disponibles
  matchdays: number[] = [];

  // journée sélectionnée (signal = réactif natif Angular 18)
  selectedJournee = signal<number>(1);

  // flux des matches à afficher
  matches$!: Observable<Match[]>;

  private code = '';
  private saison = '';

  // déclencheur pour recharger quand la journée change
  private journeeTrigger = new BehaviorSubject<number>(1);

  constructor(
    private route: ActivatedRoute,
    private matchService: MatchService
  ) {}

  /* ------------------------------------------------------------------ */
  /* matchday.component.ts — ngOnInit corrigé                            */
  /* ------------------------------------------------------------------ */
  ngOnInit(): void {
    console.log('MatchdayComponent init appelé');

    /* 1) lire les paramètres une seule fois */
    this.code   = this.route.parent!.snapshot.paramMap.get('code')!;     // ← parent
    this.saison = this.route.snapshot.paramMap.get('saison')!;           // ← courant

    /* 2) charger tous les matches de la saison */
    this.matchService.getAllMatches(this.code, this.saison)
      .pipe(take(1))
      .subscribe(all => {

        /* --- extraire les journées --- */
        this.matchdays = Array.from(
          new Set(
            all
              .map(m => m.numJournee)
              .filter((j): j is number => j !== undefined && j !== null)  // type-guard
          )
        ).sort((a, b) => a - b);

        /* --- déterminer la prochaine journée (si au moins un match) --- */
        let next = this.matchdays[0] ?? 1;   // valeur par défaut

        if (all.length) {
          const today = new Date();
          next =
            all
              .filter(
                m =>
                  new Date(m.date) >= today ||
                  m.scoreDomicile === null ||
                  m.scoreExterieur === null
              )
              .sort(
                (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()
              )[0]?.numJournee ?? this.matchdays.at(-1)!;
        }

        /* --- mettre à jour le signal + déclencher le flux --- */
        this.selectedJournee.set(next);
        this.journeeTrigger.next(next);
      });

    /* 3) flux des matches pour la journée sélectionnée */
    this.matches$ = this.journeeTrigger.pipe(
      switchMap(j =>
        this.matchService.getMatchesByJournee(this.code, this.saison, j)
      )
    );
  }


  /** appelé quand l’utilisateur change la journée dans la liste */
  onJourneeChange(j: number): void {
    this.selectedJournee.set(j);   // met à jour le signal
    this.journeeTrigger.next(j);   // relance la requête API
  }

}
