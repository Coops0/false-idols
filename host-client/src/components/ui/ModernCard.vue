<template>
  <div class="relative group h-full">
    <div
        :class="{
        'shadow-[0_4px_12px_rgb(0,0,0,0.04)]': !variant || variant === 'default',
        'shadow-[0_4px_12px_rgb(0,0,0,0.06)]': variant === 'highlighted',
        'shadow-[0_4px_12px_rgb(0,0,0,0.08)]': variant === 'danger'
      }"
        class="absolute inset-0 bg-white/80 backdrop-blur-lg rounded-lg"
    />
    <div
        :class="{
        'bg-white/50': !variant || variant === 'default',
        'bg-blue-50/50': variant === 'highlighted',
        'bg-red-50/50': variant === 'danger'
      }"
        class="relative h-full p-4 rounded-lg border border-gray-100/50"
    >
      <div class="relative z-10 h-full">
        <slot/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{ variant?: 'default' | 'highlighted' | 'danger' }>();
</script>