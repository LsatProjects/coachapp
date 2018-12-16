/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CoachappTestModule } from '../../../test.module';
import { MovementCategoryDetailComponent } from 'app/entities/movement-category/movement-category-detail.component';
import { MovementCategory } from 'app/shared/model/movement-category.model';

describe('Component Tests', () => {
    describe('MovementCategory Management Detail Component', () => {
        let comp: MovementCategoryDetailComponent;
        let fixture: ComponentFixture<MovementCategoryDetailComponent>;
        const route = ({ data: of({ movementCategory: new MovementCategory(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [MovementCategoryDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(MovementCategoryDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(MovementCategoryDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.movementCategory).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
