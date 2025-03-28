<template>
  <div
      :class="{
        'p-6': !noPadding,
        'p-0': noPadding
    }"
      class="bg-white rounded-xl shadow-lg border border-gray-200 overflow-hidden transition-all duration-200 hover:shadow-xl"
  >
    <div v-if="slots.header()" class="border-b border-gray-200 p-4">
      <slot name="header"/>
    </div>
    <div class="relative">
      <slot/>
    </div>
    <div v-if="slots.header()" class="border-t border-gray-200 p-4">
      <slot name="footer"/>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{ noPadding?: boolean }>();

const slots = defineSlots<{
  default(props: { noPadding?: boolean }): any;
  header(): any;
  footer(): any;
}>();
</script> 