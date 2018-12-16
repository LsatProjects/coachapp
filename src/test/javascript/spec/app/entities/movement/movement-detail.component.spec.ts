/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CoachappTestModule } from '../../../test.module';
import { MovementDetailComponent } from 'app/entities/movement/movement-detail.component';
import { Movement } from 'app/shared/model/movement.model';

describe('Component Tests', () => {
    describe('Movement Management Detail Component', () => {
        let comp: MovementDetailComponent;
        let fixture: ComponentFixture<MovementDetailComponent>;
        const route = ({ data: of({ movement: new Movement(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [MovementDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(MovementDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(MovementDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.movement).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
