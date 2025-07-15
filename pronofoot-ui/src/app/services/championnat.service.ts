import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Championnat } from '../models/championnat.model';
import { Equipe } from '../models/equipe.model';
import { Saison } from '../models/saison.model';

@Injectable({ providedIn: 'root' })
export class ChampionnatService {
  private readonly baseUrl = 'http://localhost:8088/api/championnats';

  constructor(private http: HttpClient) {}

  getChampionnats(): Observable<Championnat[]> {
    return this.http.get<Championnat[]>(`${this.baseUrl}`);
  }

  getSaisons(code: string): Observable<Saison[]> {
    return this.http.get<Saison[]>(`${this.baseUrl}/${code}/saisons`);
  }

  getEquipes(code: string, id: string): Observable<Equipe[]> {
    return this.http.get<Equipe[]>(`${this.baseUrl}/${code}/saisons/${id}/equipes`);
  }

  getChampionnat(code: string, sync: boolean = false): Observable<Championnat> {
    const params = { sync: sync.toString() };
    return this.http.get<Championnat>(`${this.baseUrl}/${code}`, { params });
  }
}
