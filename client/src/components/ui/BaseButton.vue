<template>
  <button
      :class="{
      'bg-gradient-to-b from-blue-500 to-blue-700 text-white': variant === 'primary',
      'bg-gradient-to-b from-gray-100 to-gray-200 text-gray-800': variant === 'secondary',
      'bg-gradient-to-b from-red-500 to-red-700 text-white': variant === 'danger',
      'bg-gradient-to-b from-white to-gray-100 text-gray-800': !variant,
      'active:translate-y-0.5': !disabled && !loading
    }"
      :disabled="disabled || loading"
      class="relative px-6 py-3 rounded-lg font-semibold transition-all duration-200 overflow-hidden shadow-md active:shadow-sm disabled:opacity-70 disabled:transform-none disabled:shadow-none"
  >
    <span class="relative z-10">
      <slot/>
    </span>
    <span v-if="loading" class="absolute inset-0 flex items-center justify-center">
      <span class="size-5 border-2 border-white/30 border-t-white rounded-full animate-spin"/>
    </span>
  </button>
</template>

<script lang="ts" setup>
defineProps<{
  variant?: 'primary' | 'secondary' | 'danger'
  disabled?: boolean
  loading?: boolean
}>();
</script> 