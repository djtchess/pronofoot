import { Component, OnInit } from '@angular/core';
import { HttpClient }        from '@angular/common/http';
import { ActivatedRoute }    from '@angular/router';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Observable }        from 'rxjs';
import { map, switchMap }    from 'rxjs/operators';

/* DTO conforme à l’endpoint -------------------------------------- */
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
  imports    : [NgIf, NgFor, AsyncPipe]
})
export class ClassementComponent implements OnInit {

  classement$!: Observable<TeamStandingDto[]>;

  constructor(
    private readonly http : HttpClient,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    /* URL attend : /championnats/:code/saisons/:id/classement ---------- */
    this.classement$ = this.route.parent!.params.pipe(
      switchMap(params => {
        const code = params['code'];
        const id   = params['id'];
        const url  = `http://localhost:8088/api/championnats/${code}/saisons/${id}/classement`;
        return this.http.get<TeamStandingDto[]>(url);
      }),
      /* ajoute la position (1,2,3, …) pour l’affichage ----------------- */
      map(list =>
        list.map((t, idx) => ({ ...t, position: idx + 1 }))
      )
    );
  }
}
