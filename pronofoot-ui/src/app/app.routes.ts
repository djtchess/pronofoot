import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'championnats', pathMatch: 'full' },
  {
    path: 'championnats',
    loadComponent: () => import('./components/championnats/championnats.component').then(m => m.ChampionnatsComponent)
  },
  {
    path: 'championnats/:code/saisons/:annee/equipes',
    loadComponent: () => import('./components/equipes/equipes.component').then(m => m.EquipesComponent)
  },
    {
    path: 'championnats/:code',
    loadComponent: () => import('./components/championnat/championnat.component').then(m => m.ChampionnatComponent)
  },
  {
    path: 'matchs',
    loadComponent: () => import('./components/matchs/matchs.component').then(m => m.MatchsComponent)
  }
];
