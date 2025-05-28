import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegisterRecruiterComponent} from './components/register-recruiter/register-recruiter.component';
import {RegisterCandidateComponent} from './components/register-candidate/register-candidate.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {TemplateComponent} from './components/template/template.component';
import {ChatComponent} from './components/messaging/chat/chat.component';
import {NewConversationComponent} from './components/messaging/new-conversation/new-conversation.component';
import {ConversationListComponent} from './components/messaging/conversation-list/conversation-list.component';
import {JobsComponent} from './components/jobs/jobs.component';
import {SettingsComponent} from './components/settings/settings.component';
import {MyProfileComponent} from './components/my-profile/my-profile.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';


const routes: Routes = [
  {
    path: '',
    component: TemplateComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'login', component: LoginComponent },
      { path: 'home', component: HomeComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'register-candidate', component: RegisterCandidateComponent },
      { path: 'register-recruiter', component: RegisterRecruiterComponent },
      {path: 'jobs', component: JobsComponent },
      {path: 'settings', component: SettingsComponent },
      {path: 'profile', component: MyProfileComponent },
      {
        path: 'messaging',
        component: ConversationListComponent,
        children: [
          { path: 'new', component: NewConversationComponent },
          { path: 'conversation/:id', component: ChatComponent }
        ]
      }
    ]
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
