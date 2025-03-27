<template>
  <div
      :class="{
      'p-6': !noPadding,
      'p-0': noPadding
    }"
      class="bg-white rounded-xl shadow-lg border border-gray-200 overflow-hidden transition-all duration-200 hover:shadow-xl"
  >
    <div v-if="$slots.header" class="border-b border-gray-200 p-4">
      <slot name="header"></slot>
    </div>
    <div class="relative">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="border-t border-gray-200 p-4">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{ noPadding?: boolean }>();
</script> 