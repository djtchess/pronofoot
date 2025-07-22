import { Saison } from "./saison.model";

export interface Championnat {
  code: string;
  nom: string;
  pays: string;
  currentSeason: Saison; 
  equipes: string[];
  saisons: Saison[];
}
