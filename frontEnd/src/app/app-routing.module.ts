import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegisterRecruiterComponent} from './components/register-recruiter/register-recruiter.component';
import {RegisterCandidateComponent} from './components/register-candidate/register-candidate.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {TemplateComponent} from './components/template/template.component';


const routes: Routes = [
  {
    path: '',
    component: TemplateComponent,
    children: [
      { path: '', redirectTo: 'login', pathMatch: 'full' },
      { path: 'login', component: LoginComponent },
      { path: 'home', component: HomeComponent },
      { path: 'register-candidate', component: RegisterCandidateComponent },
      { path: 'register-recruiter', component: RegisterRecruiterComponent },
    ]
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
