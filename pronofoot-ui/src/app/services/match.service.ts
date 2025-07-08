import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';

@Injectable({ providedIn: 'root' })
export class MatchService {
  private readonly baseUrl = '/matchs';

  constructor(private http: HttpClient) {}

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
