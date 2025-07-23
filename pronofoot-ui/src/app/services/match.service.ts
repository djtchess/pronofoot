import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';

@Injectable({ providedIn: 'root' })
export class MatchService {
  // private readonly baseUrl = '/matchs';
    private readonly baseUrl = 'http://localhost:8088/api/matches';

  constructor(private http: HttpClient) {}

    /**
   * Déclenche l’import des matches côté back‑end.
   * @param code   Code du championnat (PL, FL1, SA, …)
   * @param saison Année de début de saison (ex. 2024 pour 2024‑2025)
   */
  importMatches(code: string, saison: string): Observable<string> {
    const params = new HttpParams()
      .set('code', code)
      .set('saison', saison);

    return this.http.post(
      `${this.baseUrl}/import`, null,  { params, responseType: 'text' }
    );
  }

    /** Tous les matches d’un championnat et d’une saison */
  getAllMatches(code: string, saison: string): Observable<Match[]> {
    const params = new HttpParams().set('code', code).set('saison', saison);
    return this.http.get<Match[]>(`${this.baseUrl}/all`, { params });
  }

  /** Matches d’une journée précise */
  getMatchesByJournee(
    code: string,
    saison: string,
    journee: number
  ): Observable<Match[]> {
    const params = new HttpParams()
      .set('code', code)
      .set('saison', saison)
      .set('journee', journee);
    return this.http.get<Match[]>(`${this.baseUrl}/byJournee`, { params });
  }

  getMatchsByChampionnat(code: string, withoutScores = false): Observable<Match[]> {
    const url = withoutScores
      ? `${this.baseUrl}/championnat/${code}?sansScores=true`
      : `${this.baseUrl}/championnat/${code}`;
    return this.http.get<Match[]>(url);
  }

  updateMatch(match: Match): Observable<Match> {
    return this.http.put<Match>(`${this.baseUrl}/${match.id}`, match);
  }

  /** MAJ des scores d’une journée précise */
  updateScoresJournee(code: string,
                      annee: string,
                      journee: number) {
    return this.http.post<void>(`${this.baseUrl}/championnat/${code}/saison/${annee}/journees/${journee}/updateScores`, {});
  }

  /** MAJ de tous les scores manquants jusqu’à hier */
  updateScoresBeforeToday(code: string, annee: string) {
    return this.http.post<void>(`${this.baseUrl}/championnat/${code}/saison/${annee}/updateScoresBeforeToday`, {});
  }

}


