import { Component, OnInit } from '@angular/core';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Observable, forkJoin, map, switchMap } from 'rxjs';
import { ChampionnatService } from '../../services/championnat.service';
import { Championnat } from '../../models/championnat.model';

interface ChampionnatWithSaisons {
  championnat: Championnat;
  saisons: string[];
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
      switchMap(championnats => {
        const requests = championnats.map(c =>
          this.championnatService.getSaisons(c.code).pipe(
            map(saisons => ({ championnat: c, saisons }))
          )
        );
        return forkJoin(requests);
      })
    );
  }
}
