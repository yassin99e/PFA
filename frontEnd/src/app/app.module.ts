import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

// Components :
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { RegisterCandidateComponent } from './components/register-candidate/register-candidate.component';
import { RegisterRecruiterComponent } from './components/register-recruiter/register-recruiter.component';
import { TemplateComponent } from './components/template/template.component';
import {ChatComponent} from './components/messaging/chat/chat.component';
import {ConversationListComponent} from './components/messaging/conversation-list/conversation-list.component';
import {NewConversationComponent} from './components/messaging/new-conversation/new-conversation.component';
import {WebsocketControllerComponent} from './components/messaging/websocket-controller/websocket-controller.component';
import { JobsComponent } from './components/jobs/jobs.component';
import { MyProfileComponent } from './components/my-profile/my-profile.component';
import { SettingsComponent } from './components/settings/settings.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';


// Angular Materials
import { AppRoutingModule } from './app-routing.module';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import {MatListModule, MatNavList} from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatMenuModule, MatMenuTrigger} from '@angular/material/menu';
import { MatCardActions, MatCardContent, MatCardHeader, MatCardModule} from '@angular/material/card';
import { MatFormFieldModule} from '@angular/material/form-field';
import { MatExpansionModule } from '@angular/material/expansion';
import {MatInputModule} from '@angular/material/input';
import { MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatBadgeModule} from '@angular/material/badge';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDividerModule} from '@angular/material/divider';





@NgModule({
  declarations: [
    AppComponent,
    TemplateComponent,
    LoginComponent,
    HomeComponent,
    RegisterCandidateComponent,
    RegisterRecruiterComponent,
    ChatComponent,
    ConversationListComponent,
    NewConversationComponent,
    WebsocketControllerComponent,
    JobsComponent,
    MyProfileComponent,
    SettingsComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    FormsModule,
    MatMenuTrigger,
    MatMenuModule,
    MatExpansionModule,
    MatCardActions,
    MatCardModule,
    MatCardHeader,
    MatCardContent,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatChipsModule,
    MatBadgeModule,
    MatProgressSpinnerModule,
    MatSidenavModule,
    MatIconModule,
    MatNavList,
    ReactiveFormsModule,
    MatDividerModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})


// c'est le module principal dans lequel on importe tous les modules on a besoin dans notre application .
export class AppModule { }
