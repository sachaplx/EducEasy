<template>
  <div class="container" style="max-width: 520px; margin-top: 3rem">
    <div class="card">
      <h2>Créer un compte</h2>
      <form @submit.prevent="submit">
        <div class="grid" style="grid-template-columns: 1fr 1fr">
          <input class="input" v-model="prenom" placeholder="Prénom" />
          <input class="input" v-model="nom" placeholder="Nom" />
        </div>
        <div class="grid" style="grid-template-columns: 1fr">
          <input
            class="input"
            v-model="email"
            placeholder="Email"
            type="email"
          />
          <input
            class="input"
            v-model="username"
            placeholder="Nom d'utilisateur"
          />
          <input
            class="input"
            v-model="password"
            type="password"
            placeholder="Mot de passe"
          />
        </div>
        <small
          >Par défaut, les comptes créés sont de type
          <strong>Instituteur</strong>.</small
        >
        <div style="margin-top: 0.75rem; display: flex; gap: 0.5rem">
          <button class="btn" :disabled="loading">
            {{ loading ? "Création..." : "S'inscrire" }}
          </button>
          <router-link to="/login" class="btn secondary">Annuler</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { api } from "../services/api";
import { useRouter } from "vue-router";
const router = useRouter();
const prenom = ref("");
const nom = ref("");
const email = ref("");
const username = ref("");
const password = ref("");
const loading = ref(false);
async function submit() {
  loading.value = true;
  try {
    await api.post("/api/auth/register", {
      prenom: prenom.value,
      nom: nom.value,
      email: email.value,
      username: username.value,
      password: password.value,
    });
    alert("Compte créé ! Vous pouvez vous connecter.");
    router.push({ name: "login" });
  } catch {
    alert("Échec de la création de compte.");
  } finally {
    loading.value = false;
  }
}
</script>
