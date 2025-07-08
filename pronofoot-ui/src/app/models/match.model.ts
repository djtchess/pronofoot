import { Equipe } from './equipe.model';

export interface Match {
  id: number;
  equipeDomicile: Equipe;
  equipeExterieure: Equipe;
  scoreDomicile: number;
  scoreExterieur: number;
  date: string;
  journee?: number;
}
