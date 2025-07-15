// src/app/app.routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'championnats', pathMatch: 'full' },

  /* liste des championnats */
  {
    path: 'championnats',
    loadComponent: () =>
      import('./components/championnats/championnats.component')
        .then(m => m.ChampionnatsComponent),
  },

  /* ⚠️  nouvelle route "fallback" sans saison */
  {
    path: 'championnats/:code',
    loadComponent: () =>
      import('./components/championnat/championnat.component')
        .then(m => m.ChampionnatComponent),
  },

  /* championnat + saison précise */
  {
    path: 'championnats/:code/saisons/:id',
    loadComponent: () =>
      import('./components/championnat/championnat.component')
        .then(m => m.ChampionnatComponent),
    children: [
      {
        path: 'equipes',
        loadComponent: () =>
          import('./components/equipes/equipes.component')
            .then(m => m.EquipesComponent),
      },
    ],
  },

  /* autres routes */
  {
    path: 'matchs',
    loadComponent: () =>
      import('./components/matchs/matchs.component')
        .then(m => m.MatchsComponent),
  },
];

