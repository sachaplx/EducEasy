<template>
  <div class="container" style="max-width: 400px; margin-top: 3rem">
    <div class="card">
      <h2>Connexion</h2>
      <form @submit.prevent="submit">
        <div style="display: grid; gap: 0.5rem; margin-top: 0.5rem">
          <input
            class="input"
            v-model="username"
            placeholder="Nom d'utilisateur"
            autocomplete="username"
          />
          <input
            class="input"
            v-model="password"
            placeholder="Mot de passe"
            type="password"
            autocomplete="current-password"
          />
          <button class="btn" :disabled="loading">
            {{ loading ? "Connexion..." : "Se connecter" }}
          </button>
        </div>
      </form>
      <p style="margin-top: 0.75rem">
        Pas de compte ?
        <router-link to="/register">Créer un compte</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "../stores/auth";
const auth = useAuthStore();
const router = useRouter();
const route = useRoute();
const username = ref("");
const password = ref("");
const loading = ref(false);
async function submit() {
  loading.value = true;
  try {
    await auth.login({ username: username.value, password: password.value });
    router.push(route.query.redirect || "/");
  } catch {
    const status = e?.response?.status;
    const serverMsg = e?.response?.data?.error || e?.response?.data?.message;
    alert(
      `Échec connexion (${status ?? "network"}): ${serverMsg ?? e.message}`
    );
  } finally {
    loading.value = false;
  }
}
</script>
