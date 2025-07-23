import { Component, OnInit } from '@angular/core';
import { NgIf, NgForOf, AsyncPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ChampionnatService } from '../../services/championnat.service';
import { MatchService }        from '../../services/match.service';
import { Championnat }         from '../../models/championnat.model';
import { Match }               from '../../models/match.model';
import { Saison }              from '../../models/saison.model';

import { Observable, of, map, switchMap } from 'rxjs';

interface MatchesByJournee {
  journee: number;
  matchs: Match[];
}

@Component({
  standalone: true,
  selector   : 'app-matchs',
  templateUrl: './matchs.component.html',
  styleUrls  : ['./matchs.component.scss'],
  imports    : [NgIf, NgForOf, FormsModule, AsyncPipe, DatePipe]
})
export class MatchsComponent implements OnInit {

  /* listes asynchrones ------------------------------------------------ */
  championnats$!: Observable<Championnat[]>;

  /* états UI ----------------------------------------------------------- */
  selectedCode: string | null = null;
  saisons: number[] = [];                  // années disponibles
  selectedSeasonYear!: number;             // année courante sélectionnée

  /* matches affichés --------------------------------------------------- */
  data$: Observable<MatchesByJournee[]> = of([]);

  constructor(
    private championnatService: ChampionnatService,
    private matchService      : MatchService
  ) {}

  /* ------------------------------------------------------------------- */
  ngOnInit(): void {
    this.championnats$ = this.championnatService.getChampionnats();
  }

  /* (1) lorsqu’on change de championnat ------------------------------- */
  onSelectChampionnat(): void {
    if (!this.selectedCode) {
      this.resetView();
      return;
    }

    /* charge les saisons puis les matches */
    this.championnatService.getSaisons(this.selectedCode).pipe(
      map((s: Saison[]) => s.map(sa => +sa.year).sort((a, b) => b - a))
    ).subscribe(years => {
      this.saisons = years;
      this.selectedSeasonYear = years[0];   // saison par défaut = plus récente
      this.reloadMatches();
    });
  }

  /* (2) rechargement quand la saison change --------------------------- */
  reloadMatches(): void {
    if (!this.selectedCode || !this.selectedSeasonYear) { this.data$ = of([]); return; }

    this.data$ = this.matchService
      .getAllMatches(this.selectedCode, this.selectedSeasonYear.toString())
      .pipe(map(groupByJournee));
  }

  /* (3) bouton : maj scores d’une journée ----------------------------- */
  updateScoresJournee(journee: number): void {
    if (!this.selectedCode || !this.selectedSeasonYear) return;

    this.matchService
        .updateScoresJournee(this.selectedCode,
                             this.selectedSeasonYear.toString(),
                             journee)
        .subscribe(() => this.reloadMatches());
  }

  /* (4) bouton : maj scores avant aujourd’hui ------------------------- */
  updateScoresBeforeToday(): void {
    if (!this.selectedCode || !this.selectedSeasonYear) return;

    this.matchService
        .updateScoresBeforeToday(this.selectedCode,
                                 this.selectedSeasonYear.toString())
        .subscribe(() => this.reloadMatches());
  }

  /* utilitaires ------------------------------------------------------- */
  private resetView(): void {
    this.saisons = [];
    this.data$   = of([]);
  }
}

/* ===== helper pour grouper & trier par journée ====================== */
function groupByJournee(matches: Match[]): MatchesByJournee[] {
  const grouped: Record<number, Match[]> = {};
  matches.forEach(m => {
    const j = (m as any).journee ?? 0;
    (grouped[j] ||= []).push(m);
  });
  return Object.entries(grouped)
    .map(([j, m]) => ({ journee: +j, matchs: m }))
    .sort((a, b) => a.journee - b.journee);
}
