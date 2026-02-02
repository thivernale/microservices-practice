import { Component, computed, effect, model, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Avatar } from 'primeng/avatar';
import { Button } from 'primeng/button';
import { FloatLabel } from 'primeng/floatlabel';
import { InputNumber } from 'primeng/inputnumber';
import { ToggleSwitch } from 'primeng/toggleswitch';
import { playBeep } from '../../utils/beep/beep';

@Component({
  selector: 'app-interval-timer',
  imports: [
    Button,
    InputNumber,
    FormsModule,
    Avatar,
    ToggleSwitch,
    FloatLabel
  ],
  template: `
    @let active = isTimerActive();
    @let started = remainingTotal() > 0;
    <div class="flex flex-column flex-wrap gap-4 p-8 justify-content-center align-items-center border-bottom-1">
      <h2 class="">Interval Timer</h2>
      <div class="">
        <p-avatar size="xlarge" shape="circle" class="p-8 text-7xl shadow-8 font-bold text-green-900 bg-green-200"
                  [styleClass]="remaining() === 0 ? 'bg-white' : ''"
        >
          {{ remaining() }}
        </p-avatar>
        @if (started) {
          <div class="pt-4 text-center">
            Interval {{ rounds() - Math.floor(remainingTotal() / duration()) }} of {{ rounds() }}
          </div>
        }
      </div>
      <div class="text-center gap-2 flex my-4 align-items-end">
        <div>
          <p-float-label>
            <p-inputNumber
              [(ngModel)]="rounds"
              [disabled]="started"
              [max]="60"
              [min]="0"
              [step]="1"
              [showButtons]="true"
              placeholder="Rounds"
              buttonLayout="horizontal"
              inputId="rounds"
              inputStyleClass="w-6rem">
              <ng-template #incrementbuttoniconRounds>
                <span class="pi pi-plus"></span>
              </ng-template>
              <ng-template #decrementbuttoniconRounds>
                <span class="pi pi-minus"></span>
              </ng-template>
            </p-inputNumber>
            <label for="rounds">Number of rounds</label>
          </p-float-label>
        </div>
        <div>
          <p-float-label>
            <p-inputNumber
              [(ngModel)]="duration"
              [disabled]="started"
              [max]="120"
              [min]="10"
              [step]="5"
              [showButtons]="true"
              placeholder="Duration"
              buttonLayout="horizontal"
              inputId="duration"
              inputStyleClass="w-6rem">
              <ng-template #incrementbuttoniconDuration>
                <span class="pi pi-plus"></span>
              </ng-template>
              <ng-template #decrementbuttoniconDuration>
                <span class="pi pi-minus"></span>
              </ng-template>
            </p-inputNumber>
            <label for="duration">Interval duration</label>
          </p-float-label>
        </div>
        <p-button
          (onClick)="toggleTimerStarted()"
          [outlined]="started"
        >{{ started ? 'Stop' : 'Start' }}
        </p-button>
        @if (started) {
          <p-button
            (onClick)="toggleTimerActive()"
          >{{ active ? 'Pause' : 'Resume' }}
          </p-button>
        }
        <label class="flex gap-2">
          <p-toggle-switch name="playSound" [(ngModel)]="playSound"></p-toggle-switch>
          Sound
        </label>
      </div>
    </div>
  `,
})
export class IntervalTimerComponent {
  rounds = model(10);
  duration = model(30);
  playSound = model(true);
  remainingTotal = signal(0);
  isTimerActive = signal(false);
  remaining = computed(() => this.remainingTotal() % (this.duration() ?? 1));
  protected readonly Math = Math;
  private timeout: any;
  private targetTime = 0;

  protected toggleTimerStarted() {
    if (this.remainingTotal() > 0) {
      // stop timer
      this.remainingTotal.set(0);
      this.isTimerActive.set(false);
    } else {
      // start timer
      this.remainingTotal.set(this.duration() * this.rounds());
      this.isTimerActive.set(true);
    }
  }

  protected toggleTimerActive() {
    if (this.isTimerActive()) {
      // pause timer
      this.isTimerActive.set(false);
    } else {
      // resume timer
      this.isTimerActive.set(true);
    }
  }

  private readonly tick = () => {
    const now = Date.now();
    // Calculate true remaining time based on system clock
    const remaining = Math.max(0, Math.ceil((this.targetTime - now) / 1000));
    this.remainingTotal.set(remaining);
    // this.remainingTotal.update(value => value - 1);

    if (this.playSound() && this.remaining() <= 3) {
      playBeep(this.remaining() === 0 ? 0.5 : 0.2);
    }

    if (this.remainingTotal() <= 0) {
      this.isTimerActive.set(false);
    }
  };

  private readonly manageInterval = effect(() => {
    const active = this.isTimerActive();
    const remaining = this.remainingTotal();

    if (remaining <= 0 || !active) {
      if (this.timeout) {
        console.log("Clearing", this.timeout)
        clearTimeout(this.timeout);
        this.timeout = null;
      }
    } else if (!this.timeout) {
      this.targetTime = Date.now() + (remaining * 1000);
      this.timeout = setInterval(this.tick, 1_000) as any as number;
      console.log("Setting", this.timeout)
    }
  });
}
