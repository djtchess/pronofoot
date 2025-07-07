import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'championnats',
    pathMatch: 'full'
  },
  {
    path: 'championnats',
    loadComponent: () => import('./components/championnats/championnats.component').then(m => m.ChampionnatsComponent)
  },
  {
    path: 'championnats/:code/saisons/:annee/equipes',
    loadComponent: () => import('./components/equipes/equipes.component').then(m => m.EquipesComponent)
  }
];
