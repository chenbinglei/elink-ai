import { useRoute } from "vue-router";

export default function useSiteId() {
  const route = useRoute();
  const siteId = route.params.id;

  return {
    siteId,
  };
}
