<template>
  <div class="relative group">
    <div
        class="absolute inset-0 bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg"
        :class="{
        'shadow-[0_8px_30px_rgb(0,0,0,0.04)]': !variant || variant === 'default',
        'shadow-[0_8px_30px_rgb(0,0,0,0.08)]': variant === 'highlighted',
        'shadow-[0_8px_30px_rgb(0,0,0,0.12)]': variant === 'danger'
      }"
    />
    <div
        class="relative p-8 rounded-2xl border border-gray-100/50"
        :class="{
        'bg-white/50': !variant || variant === 'default',
        'bg-blue-50/50': variant === 'highlighted',
        'bg-red-50/50': variant === 'danger'
      }"
    >
      <div class="relative z-10">
        <slot/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{ variant?: 'default' | 'highlighted' | 'danger' }>();
</script> 