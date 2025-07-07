import { Component, OnInit } from '@angular/core';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ChampionnatService } from '../../services/championnat.service';
import { Equipe } from '../../models/equipe.model';

@Component({
  standalone: true,
  selector: 'app-equipes',
  templateUrl: './equipes.component.html',
  styleUrls: ['./equipes.component.scss'],
  imports: [NgIf, NgForOf, AsyncPipe, RouterLink],
})
export class EquipesComponent implements OnInit {
  equipes$!: Observable<Equipe[]>;

  code!: string;
  annee!: string;

  constructor(private route: ActivatedRoute, private championnatService: ChampionnatService) {}

  ngOnInit(): void {
    this.equipes$ = this.route.params.pipe(
      switchMap(params => {
        this.code = params['code'];
        this.annee = params['annee'];
        return this.championnatService.getEquipes(this.code, this.annee);
      })
    );
  }
}
