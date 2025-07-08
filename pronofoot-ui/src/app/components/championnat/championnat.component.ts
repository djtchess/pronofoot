import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ChampionnatService } from '../../services/championnat.service';
import { Championnat } from '../../models/championnat.model';
import { AsyncPipe, NgIf } from '@angular/common';
import { Observable, switchMap } from 'rxjs';

@Component({
  standalone: true,
  selector: 'app-championnat',
  templateUrl: './championnat.component.html',
  styleUrls: ['./championnat.component.scss'],
  imports: [NgIf, AsyncPipe, RouterLink]
})
export class ChampionnatComponent implements OnInit {
  championnat$!: Observable<Championnat>;

  constructor(private route: ActivatedRoute, private championnatService: ChampionnatService) {}

  ngOnInit(): void {
    this.championnat$ = this.route.params.pipe(
      switchMap(params => this.championnatService.getChampionnat(params['code']))
    );
  }
}
