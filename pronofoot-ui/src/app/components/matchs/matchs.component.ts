import { Component, OnInit } from '@angular/core';
import { NgIf, NgForOf, AsyncPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';          

import { ChampionnatService } from '../../services/championnat.service';
import { MatchService }        from '../../services/match.service';
import { Championnat }         from '../../models/championnat.model';
import { Match }               from '../../models/match.model';

import { Observable, of, map } from 'rxjs';

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
  championnats$!: Observable<Championnat[]>;
  selectedCode: string | null = null;

  data$: Observable<MatchesByJournee[]> = of([]);

  constructor(
    private championnatService: ChampionnatService,
    private matchService      : MatchService
  ) {}

  ngOnInit(): void {
    this.championnats$ = this.championnatService.getChampionnats();
  }

  onSelectChampionnat(): void {
    if (!this.selectedCode) { this.data$ = of([]); return; }

    this.data$ = this.matchService.getMatchsByChampionnat(this.selectedCode).pipe(
      map(matches => {
        const grouped: Record<number, Match[]> = {};
        matches.forEach(m => {
          const j = (m as any).journee ?? 0;
          (grouped[j] ||= []).push(m);
        });
        return Object.entries(grouped)
          .map(([j, m]) => ({ journee: +j, matchs: m }))
          .sort((a, b) => a.journee - b.journee);
      })
    );
  }

  updateScoresJournee(j: number): void {
    if (!this.selectedCode) return;
    this.matchService.updateScoresJournee(this.selectedCode, j)
        .subscribe(() => this.onSelectChampionnat());
  }

  updateScoresBeforeToday(): void {
    if (!this.selectedCode) return;
    this.matchService.updateScoresBeforeToday(this.selectedCode)
        .subscribe(() => this.onSelectChampionnat());
  }
}
