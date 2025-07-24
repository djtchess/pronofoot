// Version Angular 18.2.9 avec switch dynamique et UI propre
import { Component, Input, OnChanges, SimpleChanges, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

interface TeamStandingDto {
  position:       number;
  equipe:         string;
  joues:          number;
  gagnes:         number;
  nuls:           number;
  perdus:         number;
  butsPour:       number;
  butsContre:     number;
  goalDifference: number;
  points:         number;
}

@Component({
  standalone: true,
  selector: 'app-classement',
  templateUrl: './classement.component.html',
  styleUrls: ['./classement.component.scss'],
  imports: [NgIf, NgForOf, AsyncPipe, CommonModule]
})
export class ClassementComponent implements OnChanges {

  @Input({ required: true }) code!: string;
  @Input({ required: true }) seasonId!: string | number;

  classement$: Observable<TeamStandingDto[]> = of([]);
  selectedView = signal<'global' | 'domicile' | 'exterieur' | 'last5' | 'last10' | 'last5Home' | 'last5Away' | 'last10Home' | 'last10Away'>('global');

  constructor(private http: HttpClient) {}

  ngOnChanges(ch: SimpleChanges): void {
    this.loadClassement();
  }

  loadClassement(): void {
    if (!this.code || !this.seasonId) return;

    const baseUrl = `http://localhost:8088/api/championnats/${this.code}/saisons/${this.seasonId}/classement`;

    let url = baseUrl;
    switch (this.selectedView()) {
      case 'domicile':     url += '/domicile'; break;
      case 'exterieur':    url += '/exterieur'; break;
      case 'last5':        url += '/derniers/5'; break;
      case 'last10':       url += '/derniers/10'; break;
      case 'last5Home':    url += '/derniers/5/domicile'; break;
      case 'last5Away':    url += '/derniers/5/exterieur'; break;
      case 'last10Home':   url += '/derniers/10/domicile'; break;
      case 'last10Away':   url += '/derniers/10/exterieur'; break;
    }

    this.classement$ = this.http.get<TeamStandingDto[]>(url).pipe(
      map(list => list.map((t, idx) => ({ ...t, position: idx + 1 })))
    );
  }

  changeView(view: typeof this.selectedView extends () => infer T ? T : never): void {
    this.selectedView.set(view);
    this.loadClassement();
  }

  get classementLabel(): string {
  switch (this.selectedView()) {
    case 'global':       return 'Général';
    case 'domicile':     return 'Domicile';
    case 'exterieur':    return 'Extérieur';
    case 'last5':        return '5 dernières journées';
    case 'last10':       return '10 dernières journées';
    case 'last5Home':    return '5 dernières à domicile';
    case 'last5Away':    return '5 dernières à l’extérieur';
    case 'last10Home':   return '10 dernières à domicile';
    case 'last10Away':   return '10 dernières à l’extérieur';
    default:             return '';
  }
}
}
