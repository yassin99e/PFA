import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';


import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TemplateComponent } from './components/template/template.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { RegisterCandidateComponent } from './components/register-candidate/register-candidate.component';
import { RegisterRecruiterComponent } from './components/register-recruiter/register-recruiter.component';
import { FormsModule } from '@angular/forms';
import { MatMenuModule, MatMenuTrigger} from '@angular/material/menu';
import { MatCardActions, MatCardContent, MatCardHeader, MatCardModule} from '@angular/material/card';
import { MatFormFieldModule} from '@angular/material/form-field';
import { MatExpansionModule } from '@angular/material/expansion';
import {MatInput} from '@angular/material/input';
import {MatCheckbox} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
@NgModule({
  declarations: [
    AppComponent,
    TemplateComponent,
    LoginComponent,
    HomeComponent,
    RegisterCandidateComponent,
    RegisterRecruiterComponent
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
    MatInput,
    MatCheckbox,
    MatChipsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})


// c'est le module principal dans lequel on importe tous les modules on a besoin dans notre application .
export class AppModule { }
