<template>
  <v-container class="py-8" max-width="700">
    <v-card>
      <v-card-title class="text-h5">Créer un compte</v-card-title>

      <v-card-text>
        <v-alert
          v-if="error"
          type="error"
          variant="tonal"
          class="mb-4"
          :text="error"
        />

        <v-form ref="formRef" v-model="valid" @submit.prevent="onSubmit">
          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.email"
                label="Email"
                type="email"
                autocomplete="email"
                disabled
                variant="outlined"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.username"
                label="Nom d'utilisateur"
                autocomplete="username"
                :rules="[rules.required, rules.min3]"
                :disabled="loading"
                variant="outlined"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                label="Mot de passe"
                autocomplete="new-password"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append-inner="showPassword = !showPassword"
                :rules="[rules.required, rules.min6]"
                :disabled="loading"
                variant="outlined"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.confirm"
                :type="showPassword2 ? 'text' : 'password'"
                label="Confirmer le mot de passe"
                autocomplete="new-password"
                :append-inner-icon="showPassword2 ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append-inner="showPassword2 = !showPassword2"
                :rules="[rules.required, rules.matchPassword]"
                :disabled="loading"
                variant="outlined"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.firstName"
                label="Prénom"
                :rules="[rules.required]"
                :disabled="loading"
                variant="outlined"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.lastName"
                label="Nom"
                :rules="[rules.required]"
                :disabled="loading"
                variant="outlined"
              />
            </v-col>
          </v-row>

          <div class="d-flex ga-3 mt-4">
            <v-btn
              :loading="loading"
              :disabled="loading || !valid"
              color="primary"
              type="submit"
            >
              Créer mon compte
            </v-btn>

            <v-btn variant="text" :disabled="loading" @click="goLogin">
              J’ai déjà un compte
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { api } from "../services/api";
import { useAuthStore } from "../stores/auth";
import { useToastStore } from "../stores/toast";

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();
const toast = useToastStore();

const token = String(route.query.token || "").trim();
const valid = ref(false);
const loading = ref(false);
const error = ref(null);
const formRef = ref(null);
const showPassword = ref(false);
const showPassword2 = ref(false);

const roles = [
  { label: "Directeur", value: "PRINCIPAL" },
  { label: "Instituteur", value: "TEACHER" },
];

const form = ref({
  email: "",
  username: "",
  password: "",
  confirm: "",
  role: null,
  firstName: "",
  lastName: "",
});

const rules = {
  required: (v) => !!v || "Champ requis",
  email: (v) => /.+@.+\..+/.test(v) || "Email invalide",
  min3: (v) => (v?.length ?? 0) >= 3 || "Au moins 3 caractères",
  min6: (v) => (v?.length ?? 0) >= 6 || "Au moins 6 caractères",
  matchPassword: () =>
    form.value.password === form.value.confirm ||
    "Les mots de passe ne correspondent pas",
};

onMounted(async () => {
  if (!token) {
    error.value = "Lien d'invitation invalide.";
    return;
  }
  try {
    loading.value = true;
    const { data } = await api.get("/auth/invite", {
      params: { token, _ts: Date.now() },
    });
    form.value.email = data?.email || "";
    form.value.role = "TEACHER";
  } catch (e) {
    error.value = "Lien d'invitation invalide ou expiré.";
  } finally {
    loading.value = false;
  }
});

function goLogin() {
  router.push({ name: "login" });
}

async function onSubmit() {
  error.value = null;
  const ok = await formRef.value?.validate();
  if (!ok?.valid) return;
  if (form.value.password !== form.value.confirm) {
    error.value = "Les mots de passe ne correspondent pas.";
    return;
  }

  const payload = {
    email: form.value.email.trim(),
    username: form.value.username.trim(),
    password: form.value.password,
    role: String(form.value.role || "")
      .toUpperCase()
      .trim(),
    firstName: form.value.firstName.trim(),
    lastName: form.value.lastName.trim(),
  };

  try {
    loading.value = true;
    const res = await api.post("/auth/invite/complete", {
      token,
      username: form.value.username.trim(),
      password: form.value.password,
      prenom: form.value.firstName.trim(),
      nom: form.value.lastName.trim(),
    });
    toast.success(
      res.data?.message ?? "Compte créé. Vérifiez vos e-mails pour l'activer.",
    );
    localStorage.setItem("prefillEmail", form.value.email);
    router.push({ name: "login" });
  } catch (e) {
    const msg =
      e?.response?.data?.message || e?.response?.data?.error || e?.message;
    if (typeof msg === "string") error.value = msg;
    else error.value = "Inscription impossible. Vérifiez les champs.";
  } finally {
    loading.value = false;
  }
}
</script>
