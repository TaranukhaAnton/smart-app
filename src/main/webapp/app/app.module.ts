import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { SmartAppSharedModule } from 'app/shared/shared.module';
import { SmartAppCoreModule } from 'app/core/core.module';
import { SmartAppAppRoutingModule } from './app-routing.module';
import { SmartAppHomeModule } from './home/home.module';
import { SmartAppEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    SmartAppSharedModule,
    SmartAppCoreModule,
    SmartAppHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    SmartAppEntityModule,
    SmartAppAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class SmartAppAppModule {}
