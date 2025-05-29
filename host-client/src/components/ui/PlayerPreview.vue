<template>
  <div :class="sizeClasses.card">
    <div class="flex flex-col">
      <div class="flex items-center gap-2 flex-col">
        <div
            :class="[
                iconVariant === 'satan' && 'drop-shadow-red-500/80 drop-shadow-xl',
                isChancellor && 'border-2 border-yellow-500',
                isPlayingDeathAnimation && 'death-animation'
             ]"
            class="relative flex-shrink-0"
        >
          <div :class="sizeClasses.icon">
            <img
                :src="icon"
                class="size-full object-cover player-icon"
            />
          </div>
        </div>
        <div class="flex-1 min-w-0">
          <p :class="sizeClasses.name" class="font-mono text-gray-700 truncate">{{ player.name }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { GamePlayer, Player } from '@/game/state.ts';
import { computed, ref, watch } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

export type IconVariant = 'normal' | 'angel' | 'demon' | 'satan' | 'dead';

const props = defineProps<{
  player: GamePlayer | Player;
  iconVariant?: IconVariant;
  ignoreModifiers?: boolean;
  isChancellor?: boolean;
  size?: 'sm' | 'md' | 'lg' | 'xl' | '2xl';
}>();

const sizeClasses = computed(() => {
  switch (props.size ?? 'md') {
    case 'sm':
      return {
        card: 'p-2',
        icon: 'size-12',
        name: 'text-xs',
        crown: 'size-4 text-[8px]'
      };
    case 'md':
      return {
        card: 'p-3',
        icon: 'size-16',
        name: 'text-sm',
        crown: 'size-5 text-xs'
      };
    case 'lg':
      return {
        card: 'p-4',
        icon: 'size-24',
        name: 'text-lg',
        crown: 'size-6 text-sm'
      };
    case 'xl':
      return {
        card: 'p-6',
        icon: 'size-32',
        name: 'text-2xl',
        crown: 'size-7 text-sm'
      };
    case '2xl':
      return {
        card: 'p-8',
        icon: 'size-40',
        name: 'text-3xl',
        crown: 'size-8 text-base'
      };
  }
});

const isGamePlayer = computed(() => 'role' in props.player);
const isDead = computed(() => !props.ignoreModifiers && isGamePlayer.value && !(props.player as GamePlayer).is_alive);

const icon = computed(() => {
  let variant = props.iconVariant;
  if (!variant && !props.ignoreModifiers) {
    if (isDead.value) {
      variant = 'dead';
    }
  }

  const i = props.player.icon;
  switch (variant ?? 'normal') {
    case 'normal':
      return PlayerIcon.normal(i);
    case 'angel':
      return PlayerIcon.angel(i);
    case 'demon':
      return PlayerIcon.demon(i);
    case 'satan':
      return PlayerIcon.satan(i);
    case 'dead':
      return PlayerIcon.dead(i);
  }
});


const isPlayingDeathAnimation = ref(false);
watch(() => (props.player as GamePlayer)?.is_alive, (isAliveNow, wasAliveBefore) => {
  if (wasAliveBefore && !isAliveNow) {
    isPlayingDeathAnimation.value = true;
    setTimeout(() => (isPlayingDeathAnimation.value = false), 2000);
  }
});
</script>

<style scoped>
img {
  image-rendering: pixelated;
}

/* @formatter:off */
@keyframes deathShake {
  0%, 100% { transform: translateX(0) scale(1); }
  10% { transform: translateX(-10px) scale(1.1); }
  20% { transform: translateX(10px) scale(1.05); }
  30% { transform: translateX(-8px) scale(1.08); }
  40% { transform: translateX(8px) scale(1.03); }
  50% { transform: translateX(-5px) scale(1.06); }
  60% { transform: translateX(5px) scale(1.02); }
  70% { transform: translateX(-3px) scale(1.04); }
  80% { transform: translateX(3px) scale(1.01); }
  90% { transform: translateX(-1px) scale(1.02); }
}

@keyframes deathFade {
  0% { opacity: 1; filter: brightness(1) contrast(1); }
  25% { opacity: 0.8; filter: brightness(1.5) contrast(1.2); }
  50% { opacity: 0.6; filter: brightness(0.8) contrast(1.5) saturate(0.5); }
  75% { opacity: 0.4; filter: brightness(0.6) contrast(2) saturate(0.2); }
  100% { opacity: 0.3; filter: brightness(0.4) contrast(3) saturate(0) grayscale(1); }
}

/* @formatter:on */

.death-animation {
  animation: deathShake 1s ease-in-out,
  deathFade 2s ease-in-out;
  animation-fill-mode: forwards;
  z-index: 10;
}

.death-animation::after {
  content: '💀';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 2rem;
  animation: skullPop 0.5s ease-out 0.5s both;
  z-index: 20;
}

@keyframes skullPop {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0) rotate(-180deg);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.3) rotate(-90deg);
  }
  100% {
    opacity: 0.9;
    transform: translate(-50%, -50%) scale(1) rotate(0deg);
  }
}
</style>