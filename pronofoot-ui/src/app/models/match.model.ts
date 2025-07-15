import { Equipe } from './equipe.model';

export interface Match {
  id: number;
  equipeDomicile: Equipe;
  equipeExterieur: Equipe;
  scoreDomicile: number;
  scoreExterieur: number;
  date: string;
  numJournee: number;
}
