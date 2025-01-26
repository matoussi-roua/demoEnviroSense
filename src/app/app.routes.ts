import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { DashboardService } from './dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardService },
];

