import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './core/home/home.component';
import { HeaderComponent } from './core/header/header.component';
import { Route, RouterModule } from '@angular/router';
import { SideBarComponent } from './core/side-bar/side-bar.component';
import { ViewDetailComponent } from './view-detail/view-detail.component';
import { HomeProductsViewComponent } from './home-products-view/home-products-view.component';

const ROUTES: Route[] = [
  { path: '', component: HomeComponent,
    children:[
    ]
  }
]

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    HeaderComponent,
    SideBarComponent,
    ViewDetailComponent,
    HomeProductsViewComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule.forRoot(ROUTES)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
