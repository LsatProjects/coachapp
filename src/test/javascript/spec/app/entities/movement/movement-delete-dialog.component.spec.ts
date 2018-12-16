/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CoachappTestModule } from '../../../test.module';
import { MovementDeleteDialogComponent } from 'app/entities/movement/movement-delete-dialog.component';
import { MovementService } from 'app/entities/movement/movement.service';

describe('Component Tests', () => {
    describe('Movement Management Delete Component', () => {
        let comp: MovementDeleteDialogComponent;
        let fixture: ComponentFixture<MovementDeleteDialogComponent>;
        let service: MovementService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [MovementDeleteDialogComponent]
            })
                .overrideTemplate(MovementDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(MovementDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(MovementService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
