import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Championnat } from '../models/championnat.model';
import { Equipe } from '../models/equipe.model';

@Injectable({
  providedIn: 'root'
})
export class ChampionnatService {

  private readonly baseUrl = 'http://localhost:8088/api/championnats';

  constructor(private http: HttpClient) { }

  /**
   * Récupère tous les championnats.
   */
  getChampionnats(): Observable<Championnat[]> {
    return this.http.get<Championnat[]>(`${this.baseUrl}`);
  }

  /**
   * Récupère les saisons disponibles pour un championnat.
   * @param code code du championnat (ex.: 'L1', 'PL')
   */
  getSaisons(code: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/${code}/saisons`);
  }

  /**
   * Récupère les équipes d'un championnat pour une saison donnée.
   * @param code code du championnat
   * @param annee année de la saison (ex.: '2024')
   */
  getEquipes(code: string, annee: string): Observable<Equipe[]> {
    return this.http.get<Equipe[]>(`${this.baseUrl}/${code}/saisons/${annee}/equipes`);
  }
}
