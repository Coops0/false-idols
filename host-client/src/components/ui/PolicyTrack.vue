<template>
  <div class="relative rounded-lg border-2 p-6 overflow-hidden"
       :class="[
         variant === 'angel' 
           ? 'bg-blue-500/90 border-blue-600 bg-[radial-gradient(circle_at_center,theme(colors.blue.400),theme(colors.blue.600))]' 
           : 'border-red-700 bg-[radial-gradient(circle_at_center,theme(colors.orange.500),theme(colors.red.600))]'
       ]">
    <div class="absolute inset-0 grid grid-cols-6 gap-px opacity-10">
      <div v-for="i in 12" :key="i" class="border-r border-t border-white/20"></div>
    </div>

    <div class="relative">
      <div class="text-white text-lg font-bold mb-4 text-center tracking-wider">
        {{ variant === 'angel' ? 'ANGELS' : 'DEMONS' }}
      </div>

      <div class="grid" :class="[variant === 'angel' ? 'grid-cols-5' : 'grid-cols-6']">
        <template v-for="(position, index) in positions" :key="index">
          <div v-if="position.empty" class="col-span-1"></div>
          <div v-else class="flex flex-col items-center gap-2">
            <div class="w-8 h-8 rounded-full border-2 border-white/50 flex items-center justify-center"
                 :class="{'bg-white/20': position.number <= cardsPlayed}">
              <span class="text-white font-bold">{{ position.number }}</span>
            </div>

            <div v-if="position.icon" class="text-white/90">
              <span class="material-symbols-outlined text-lg">{{ position.icon }}</span>
            </div>

            <div v-if="position.label" class="text-white/80 text-xs text-center">
              {{ position.label }}
            </div>

            <div v-if="position.number && position.number <= cardsPlayed && descriptions[index]"
                 class="absolute top-full mt-2 text-xs text-white/90 bg-black/40 p-2 rounded whitespace-normal max-w-[150px] text-center">
              {{ descriptions[index] }}
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Position {
  number: number;
  icon?: string;
  label?: string;
  empty?: boolean;
}

interface Props {
  variant: 'angel' | 'demon';
  cardsPlayed: number;
  playerCount: number;
  descriptions: string[];
}

const props = defineProps<Props>();

const positions = computed<Position[]>(() => {
  if (props.variant === 'angel') {
    return Array(5)
        .fill(null)
        .map((_, i) => ({ number: i + 1, empty: false }));
  }

  const actions: Record<number, Position[]> = {
    5: [
      { number: 0, empty: true }, { number: 0, empty: true },
      { number: 3, icon: 'visibility', label: 'Peek', empty: false },
      { number: 4, icon: 'skull', label: 'Kill', empty: false },
      { number: 5, icon: 'skull', label: 'Kill', empty: false },
      { number: 6, icon: 'military_tech', label: 'Win', empty: false }
    ],
    7: [
      { number: 0, empty: true },
      { number: 2, icon: 'search', label: 'Investigate', empty: false },
      { number: 3, icon: 'crown', label: 'Next Pres.', empty: false },
      { number: 4, icon: 'skull', label: 'Kill', empty: false },
      { number: 5, icon: 'skull', label: 'Kill', empty: false },
      { number: 6, icon: 'military_tech', label: 'Win', empty: false }
    ],
    9: [
      { number: 1, icon: 'search', label: 'Investigate', empty: false },
      { number: 2, icon: 'search', label: 'Investigate', empty: false },
      { number: 3, icon: 'crown', label: 'Next Pres.', empty: false },
      { number: 4, icon: 'skull', label: 'Kill', empty: false },
      { number: 5, icon: 'skull', label: 'Kill', empty: false },
      { number: 6, icon: 'military_tech', label: 'Win', empty: false }
    ]
  };

  const count = props.playerCount <= 6 ? 5 : props.playerCount <= 8 ? 7 : 9;
  return actions[count];
});
</script> 