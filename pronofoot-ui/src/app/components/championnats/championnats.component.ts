import { Component, OnInit } from '@angular/core';
import { NgIf, NgForOf, AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Observable, forkJoin, map, switchMap } from 'rxjs';

import { ChampionnatService } from '../../services/championnat.service';
import { Championnat } from '../../models/championnat.model';
import { Saison } from '../../models/saison.model';

interface ChampionnatWithSaisons {
  championnat: Championnat & { currentSeason: Saison | null };
  saisons: Saison[];            // peut être []
  defaultSeasonId: number | null;
}

@Component({
  standalone: true,
  selector: 'app-championnats',
  templateUrl: './championnats.component.html',
  styleUrls: ['./championnats.component.scss'],
  imports: [NgIf, NgForOf, AsyncPipe, RouterLink],
})
export class ChampionnatsComponent implements OnInit {
  data$!: Observable<ChampionnatWithSaisons[]>;

  constructor(private championnatService: ChampionnatService) {}

  ngOnInit(): void {
    this.data$ = this.championnatService.getChampionnats().pipe(
      switchMap((championnats) =>
        forkJoin(
          championnats.map((c) =>
            this.championnatService.getSaisons(c.code).pipe(
              map((saisons) => this.buildViewModel(c, saisons))
            )
          )
        )
      )
    );
  }

  private buildViewModel(
    c: Championnat,
    saisons: Saison[]
  ): ChampionnatWithSaisons {
    const ordered = [...saisons].sort((a, b) => b.year - a.year);
    const current =
      ordered.find((s) => (s as any).isCurrent) ?? ordered[0] ?? null;

    /* si aucune saison, defaultSeasonId = null */
    const defaultId = current?.id ?? null;

    return {
      championnat: { ...c, currentSeason: current },
      saisons: ordered,
      defaultSeasonId: defaultId,
    };
  }
}
