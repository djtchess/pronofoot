import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterOutlet } from '@angular/router';
import { ChampionnatService } from '../../services/championnat.service';
import { Championnat } from '../../models/championnat.model';
import { AsyncPipe, NgIf } from '@angular/common';
import { Observable, switchMap, BehaviorSubject, take } from 'rxjs';
import { MatchService } from '../../services/match.service';

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


  constructor(private route: ActivatedRoute, private championnatService: ChampionnatService, private matchService: MatchService) {}

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

  /** Bouton « Synchroniser avec l’API » */
  onSync(): void {
    // 1) récupérer l’année de la saison courante pour le paramètre `saison`
    this.championnat$
      .pipe(take(1))
      .subscribe(champ => {
        const saison = champ.currentSeason.year.toString();

        // 2) appeler l’endpoint d’import
        this.matchService.importMatches(this.code, saison).subscribe({
          next: () => {
            // 3) puis rafraîchir l’affichage en demandant une sync complète
            this.reloadChampionnat(true);
          },
          error: err => console.error('Erreur lors de l’import des matchs :', err)
        });
      });
  }
}


