import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { CoachappSportModule } from './sport/sport.module';
import { CoachappMovementCategoryModule } from './movement-category/movement-category.module';
import { CoachappMovementModule } from './movement/movement.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        CoachappSportModule,
        CoachappMovementCategoryModule,
        CoachappMovementModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CoachappEntityModule {}
