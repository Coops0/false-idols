<template>
  <div class="relative group">
    <div
        class="absolute inset-0 bg-gradient-to-br from-amber-100 to-amber-200 rounded-lg shadow-lg"/>
    <div class="relative p-6 border-2 border-amber-300 rounded-lg bg-gradient-to-br from-amber-50 to-amber-100">
      <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-amber-400 to-amber-200"/>
      <div class="absolute bottom-0 left-0 w-full h-1 bg-gradient-to-r from-amber-200 to-amber-400"/>
      <div class="absolute left-0 top-0 bottom-0 w-1 bg-gradient-to-b from-amber-400 to-amber-200"/>
      <div class="absolute right-0 top-0 bottom-0 w-1 bg-gradient-to-b from-amber-200 to-amber-400"/>

      <div class="relative z-10">
        <slot/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  variant?: 'default' | 'highlighted' | 'danger'
}>();
</script> 