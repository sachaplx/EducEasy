<template>
  <v-container class="py-8" style="max-width: 520px">
    <v-card>
      <v-card-title>Connexion</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="submit">
          <v-text-field
            v-model="username"
            label="Email ou nom d'utilisateur"
            :rules="[rules.required]"
            variant="outlined"
            :disabled="loading"
            class="mb-4"
            hide-details="auto"
          />
          <v-text-field
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            label="Mot de passe"
            variant="outlined"
            :rules="[rules.required]"
            :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append-inner="showPassword = !showPassword"
            :disabled="loading"
            class="mb-4"
            hide-details="auto"
          />
          <v-btn
            type="submit"
            color="primary"
            :loading="loading"
            :disabled="loading"
            block
          >
            {{ loading ? "Connexion..." : "Se connecter" }}
          </v-btn>
        </v-form>
        <div class="d-flex ga-3 mt-4 align-right justify-end">
          <v-btn variant="tonal" color="primary" @click="onRegister()"
            >Créer un compte</v-btn
          >
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import { useRouter, useRoute, RouterLink } from "vue-router";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();

const username = ref("");
const password = ref("");
const loading = ref(false);
const showPassword = ref(false);

const rules = {
  required: (v) => !!v || "Ce champ est requis",
};

function onRegister() {
  router.push("register");
}

async function submit() {
  loading.value = true;
  try {
    await auth.login({
      username: username.value,
      password: password.value,
    });
    router.push(route.query.redirect || "/");
  } catch (e) {
    const s = e?.response?.status;
    const m = e?.response?.data?.error || e?.message;
    alert(`Échec connexion (${s ?? "network"}): ${m ?? "invalid credentials"}`);
  } finally {
    loading.value = false;
  }
}
</script>
