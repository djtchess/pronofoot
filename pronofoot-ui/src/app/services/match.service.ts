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

  getMatchsByChampionnat(code: string, withoutScores = false): Observable<Match[]> {
    const url = withoutScores
      ? `${this.baseUrl}/championnat/${code}?sansScores=true`
      : `${this.baseUrl}/championnat/${code}`;
    return this.http.get<Match[]>(url);
  }

  updateMatch(match: Match): Observable<Match> {
    return this.http.put<Match>(`${this.baseUrl}/${match.id}`, match);
  }

  updateScoresJournee(code: string, journee: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/championnat/${code}/journees/${journee}/updateScores`, {});
  }

  updateScoresBeforeToday(code: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/championnat/${code}/updateScoresBeforeToday`, {});
  }
}
