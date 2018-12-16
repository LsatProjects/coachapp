/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { CoachappTestModule } from '../../../test.module';
import { MovementUpdateComponent } from 'app/entities/movement/movement-update.component';
import { MovementService } from 'app/entities/movement/movement.service';
import { Movement } from 'app/shared/model/movement.model';

describe('Component Tests', () => {
    describe('Movement Management Update Component', () => {
        let comp: MovementUpdateComponent;
        let fixture: ComponentFixture<MovementUpdateComponent>;
        let service: MovementService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [MovementUpdateComponent]
            })
                .overrideTemplate(MovementUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(MovementUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(MovementService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Movement(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.movement = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Movement();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.movement = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
