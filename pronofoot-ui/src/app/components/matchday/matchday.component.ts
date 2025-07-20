// src/app/components/matchday/matchday.component.ts
import {
  Component, OnInit, signal
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  AsyncPipe, DatePipe, KeyValuePipe, NgFor, NgIf, KeyValue
} from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  BehaviorSubject, Observable, map, switchMap, take
} from 'rxjs';

import { MatchService } from '../../services/match.service';
import { Match        } from '../../models/match.model';

@Component({
  standalone : true,
  selector   : 'app-matchday',
  templateUrl: './matchday.component.html',
  styleUrls  : ['./matchday.component.scss'],
  imports    : [NgIf, NgFor, AsyncPipe, DatePipe, KeyValuePipe, FormsModule]
})
export class MatchdayComponent implements OnInit {

  /* ───────── liste et sélection de journées ───────── */
  matchdays: number[] = [];
  selectedJournee = signal<number>(1);                 // signal Angular 18

  /* ───────── flux des matches groupés par date ───────── */
  groupedByDate$!: Observable<Record<string, Match[]>>;

  private readonly journeeTrigger = new BehaviorSubject<number>(1);

  /* ───────── paramètres d’URL ───────── */
  private code   = '';
  private saison = '';

  constructor(
    private readonly route        : ActivatedRoute,
    private readonly matchService : MatchService
  ) {}

  /* ───────── cycle de vie ───────── */
  ngOnInit(): void {

    /* 1) lire les paramètres */
    this.code   = this.route.parent!.snapshot.paramMap.get('code')!;
    this.saison = this.route.snapshot.paramMap.get('saison')!;

    /* 2) récupérer tous les matches pour lister les journées disponibles */
    this.matchService.getAllMatches(this.code, this.saison).pipe(take(1))
      .subscribe(all => {

        this.matchdays = [...new Set(
          all.filter(m => m.numJournee != null).map(m => m.numJournee!)
        )].sort((a, b) => a - b);

        /* choisir la première journée à venir / incomplète, sinon la dernière */
        const today = new Date();
        const next =
          all.filter(m =>
                m.numJournee != null &&
               (new Date(m.date) >= today ||
                m.scoreDomicile == null || m.scoreExterieur == null))
             .sort((a, b) =>
               new Date(a.date).getTime() - new Date(b.date).getTime())[0]
            ?.numJournee ?? this.matchdays.at(-1) ?? 1;

        this.selectedJournee.set(next);
        this.journeeTrigger.next(next);
      });

    /* 3) flux : matches d’une journée → regroupés par date ISO */
    this.groupedByDate$ = this.journeeTrigger.pipe(
      switchMap(j =>
        this.matchService.getMatchesByJournee(this.code, this.saison, j).pipe(
          map(list =>
            list.reduce<Record<string, Match[]>>((acc, m) => {
              const key = new Date(m.date).toISOString().slice(0, 10); // yyyy-MM-dd
              (acc[key] ||= []).push(m);
              return acc;
            }, {})
          )
        )
      )
    );
  }

  /* ───────── events ───────── */

  /** sélection dans la liste déroulante */
  onJourneeChange(j: number): void {
    this.selectedJournee.set(j);
    this.journeeTrigger.next(j);
  }

  /* ───────── helpers pour le template ───────── */

  sortByDate = (a: KeyValue<string, Match[]>,
                b: KeyValue<string, Match[]>): number =>
    a.key.localeCompare(b.key);        // clé = yyyy-MM-dd croissant
}
