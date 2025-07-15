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
  id!: string;

  constructor(private route: ActivatedRoute, private championnatService: ChampionnatService) {}

  ngOnInit(): void {
    console.log('EquipesComponent appelé : '+this.code+' - '+this.id);
    this.equipes$ = this.route.parent!.params.pipe(
      switchMap(params => {
        this.code = params['code'];
        this.id = params['id'];
        return this.championnatService.getEquipes(this.code, this.id);
      })
    );
  }
}
