<template>
  <div class="min-h-screen flex flex-col">
    <AppNavbar />
    <main class="flex-1 p-4">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import AppNavbar from "./components/AppNavbar.vue";
</script>
