import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterOutlet } from '@angular/router';
import { ChampionnatService } from '../../services/championnat.service';
import { Championnat } from '../../models/championnat.model';
import { AsyncPipe, NgIf } from '@angular/common';
import { Observable, switchMap, BehaviorSubject } from 'rxjs';

@Component({
  standalone: true,
  selector: 'app-championnat',
  templateUrl: './championnat.component.html',
  styleUrls: ['./championnat.component.scss'],
  imports: [NgIf, AsyncPipe, RouterLink, RouterOutlet]
})
export class ChampionnatComponent implements OnInit {
  championnat$!: Observable<Championnat>;
  private syncTrigger = new BehaviorSubject<boolean>(false);
  private code!: string;


  constructor(private route: ActivatedRoute, private championnatService: ChampionnatService) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.code = params['code'];

      // Initialiser championnat$ ici, une seule fois
      this.championnat$ = this.syncTrigger.pipe(
        switchMap(syncValue => this.championnatService.getChampionnat(this.code, syncValue))
      );

      // Lancer un premier chargement
      this.syncTrigger.next(false);
    });
  }

  reloadChampionnat(sync: boolean): void {
    this.syncTrigger.next(sync); // déclenche la requête
  }

  onSync(): void {
    this.reloadChampionnat(true);
  }
}


