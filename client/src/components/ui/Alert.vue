<template>
  <div :class="containerClasses">
    <div class="flex">
      <div class="flex-shrink-0">
        <img :src="iconComponent" :class="iconClasses"/>
      </div>
      <div class="ml-3">
        <p :class="textClasses">
          <slot/>
        </p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import ErrorIcon from '@/assets/icons/error.svg';
import WarningIcon from '@/assets/icons/warning.svg';
import InfoIcon from '@/assets/icons/info.svg';
import SuccessIcon from '@/assets/icons/success.svg';

const ICON_COMPONENTS = {
  error: ErrorIcon,
  warning: WarningIcon,
  info: InfoIcon,
  success: SuccessIcon
};

const props = withDefaults(defineProps<{
  type?: 'error' | 'warning' | 'info' | 'success';
  size?: 'small' | 'medium' | 'large';
}>(), { type: 'info', size: 'medium' });

const typeStyles = {
  error: {
    container: 'bg-red-50 border-red-400 border-l-4',
    icon: 'text-red-400',
    text: 'text-red-800'
  },
  warning: {
    container: 'bg-amber-50 border-amber-400 border-l-4',
    icon: 'text-amber-400',
    text: 'text-amber-800'
  },
  info: {
    container: 'bg-blue-50 border-blue-400 border-l-4',
    icon: 'text-blue-400',
    text: 'text-blue-800'
  },
  success: {
    container: 'bg-green-50 border-green-400 border-l-4',
    icon: 'text-green-400',
    text: 'text-green-800'
  }
};

const sizeStyles = {
  small: {
    container: 'p-3',
    icon: 'h-4 w-4',
    text: 'text-xs'
  },
  medium: {
    container: 'p-4',
    icon: 'h-5 w-5',
    text: 'text-sm'
  },
  large: {
    container: 'p-5',
    icon: 'h-6 w-6',
    text: 'text-base'
  }
};

const containerClasses = computed(() => [typeStyles[props.type].container, sizeStyles[props.size].container]);
const iconClasses = computed(() => [typeStyles[props.type].icon, sizeStyles[props.size].icon]);
const textClasses = computed(() => [typeStyles[props.type].text, sizeStyles[props.size].text]);
const iconComponent = computed(() => ICON_COMPONENTS[props.type]);
</script>