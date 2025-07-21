// src/app/components/classement/classement.component.ts
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

interface TeamStandingDto {
  position:        number;
  equipe:       string;
  joues:           number;
  gagnes:          number;
  nuls:            number;
  perdus:          number;
  butsPour:        number;
  butsContre:      number;
  goalDifference:  number;
  points:          number;
}

@Component({
  standalone : true,
  selector   : 'app-classement',
  templateUrl: './classement.component.html',
  styleUrls  : ['./classement.component.scss'],
  imports    : [NgIf, NgForOf, AsyncPipe]
})
export class ClassementComponent implements OnChanges {

  @Input({ required: true }) code!: string;
  @Input({ required: true }) seasonId!: string | number;

  classement$: Observable<TeamStandingDto[]> = of([]);

  constructor(private http: HttpClient) {}

  ngOnChanges(ch: SimpleChanges): void {
    if (this.code && this.seasonId) {
      const url = `http://localhost:8088/api/championnats/${this.code}/saisons/${this.seasonId}/classement`;
      this.classement$ = this.http.get<TeamStandingDto[]>(url).pipe(
        map(list => list.map((t, idx) => ({ ...t, position: idx + 1 })))
      );
    }
  }
}
4