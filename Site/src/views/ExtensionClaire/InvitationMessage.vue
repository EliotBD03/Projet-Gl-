<template>
    <div class="main">
      <div class="header">
        <MainHeader text="Invitations"/>
      </div>
      <div class="notifs">
        <div class="notif" v-for="invitation in listInvitation" :key="invitation.id">
					<p>{{ $t("proposal.address") }} : {{ invitation.address }}</p>
					<p v-if="invitation.permission == 'R'">Permission : {{ $t("GestionExtClaire.R") }}</p>
					<p v-else>Permission : {{ $t("GestionExtClaire.RW") }}</p>
						<div v-if="invitation.type == 'request'">
							<p>{{ $t("GestionExtClaire.invitationPropose") }} {{ invitation.nameSender }}</p>
							<div @click.prevent.left="accept(invitation.invitationId)">
							<GoButton text="GestionExtClaire.requestAccept" :colore="'green'"/>
							</div>
							<div @click.prevent.left="refuse(invitation.invitationId)">
							<GoButton text="GestionExtClaire.requestRefuse" :colore="'red'"/>
							</div>
						</div>
						<div v-else-if="invitation.type == 'accept'">
							<p>{{ $t("GestionExtClaire.invitationAccept") }} {{ invitation.nameSender }}</p>
							<div @click.prevent.left="seen(invitation.invitationId)">
							<GoButton text="GestionExtClaire.requestSeen" :colore="'#B1B9FC'"/>
							</div>
						</div>
						<div v-else>
							<p>{{ $t("GestionExtClaire.invitationRefuse") }} {{ invitation.nameSender }}</p>
							<div @click.prevent.left="seen(invitation.invitationId)">
							<GoButton text="GestionExtClaire.requestSeen" :colore="'#B1B9FC'"/>
							</div>
						</div>
        </div>
        <div v-if="notLastPage()" @click.prevent.left="loader()">
          <GoButton text="button.seemore" :colore="'#B1B9FC'"/>
        </div>
      </div>
      <div class="bottombutton" @click.prevent.left="refresh()">
        <GoButton text="button.refresh" :colore="'#B1B9FC'"/>
      </div>
			<div class="bottombutton" @click.prevent.left="$router.push({ name: 'HomeClient' })">
        <GoButton text="header.home" :colore="'#B1B9FC'"/>
      </div>
    </div>
</template>
<script>
		/**
    * @author Extension Claire
    */
    import MainHeader from "@/components/MainHeader.vue";
    import GoButton from "@/components/GoButton.vue";
    import Swal from 'sweetalert2';
    import GlobalMethods from "@/components/GlobalMethods.vue";
		import Promise from 'bluebird';
    export default {
      components : {
        GoButton,
        MainHeader,
      },
      data(){
        return{
          linkApi : "https://babawallet.alwaysdata.net/api/client/invitedWallets/invitations/",
          nbr : 1,
          loading : false,
          lastPage : 0,
          listInvitation: []
        }},
      /*Au moment de la création on récupère déjà la première page des invitations du client */
      created() {
        this.getPage();
        GlobalMethods.getCurrentLanguage();
      },
      methods: {
        /**
        * Cette méthode permet de récupérer les pages des invitations du client avec le bouton seeMore (+à la création de la page).
        * 
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
        */
        async getPage(){
          const requestOptions = {
            method: "GET",
            headers: {'Authorization' : this.$cookies.get("token")},
          };
          this.loading = true; //bloquer les demandes de loader pendant ce temps.
          try {
            const response = await fetch(`${this.linkApi}page?page=${this.nbr}&limit=3`, requestOptions);
            if (!response.ok) { 
              const data = await response.json();
              throw new Error(data.error);
            } else {
              const data = await response.json(); 
              this.lastPage = data.last_page;
              if(this.lastPage == 0){
                  this.loading = true;
                  Swal.fire(this.$t("GestionExtClaire.noInvitation"));
              }
              else if(this.lastPage >= this.nbr){
                this.listInvitation.push(data.invitations); //ajouter la suite de la réponse à la liste
                this.listInvitation = this.listInvitation.flat(); //transforme une liste multidimensionnelle en une liste à une seule dimension
                this.loading = false;
              }
            }
          } catch(error) {
            if(error.message === "error.unauthorizedAccess")
              GlobalMethods.errorToken();
            else
              GlobalMethods.errorApi(error.message);
          }
        },
        /*Lorsque l'utilisateur appuie sur SeeMore, cette méthode est appelée 
        pour augmenter le nombre de la page et appeler getPage*/
        loader()
        {
           if(!this.loading)
          {
            this.nbr++;
            this.getPage();
          }
        },
        /*Méthode permettant de vérifier si la dernière page n'a pas encore été chargée 
        ou si on est pas en cours de chargement*/
        notLastPage(){
          if(this.lastPage == this.nbr || this.loading == true){
            return false;
          }
          return true;
        },
				/**
        * Cette méthode permet de refresh la page et obtenir les nouvelles invitations (ou ne plus afficher les anciennes).
        */
				async refresh(){
					await Promise.delay(2000); //Ce délai permet d'afficher les pop-ups s'il y en a
					this.nbr = 1;
          this.listInvitation = [];
          this.lastPage = 0;
          this.loading = false;
          await this.getPage();
				},
				/**
        * Cette méthode permet d'envoyer l'id d'invitation vers l'api afin d'accepter une invitation.
        * 
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
        */
				accept(invitationId){
					const requestOptions = {
              method: "POST",
              headers: {'Authorization': this.$cookies.get("token")}
            };
            fetch(`https://babawallet.alwaysdata.net/api/client/invitedWallets/acceptInvitation/${invitationId}`, requestOptions)
              .then(response => {
                if(!response.ok){
                  return response.json().then(json => Promise.reject(json));
                }
                else{
									Swal.fire({
										icon: 'success',
										title: this.$t('alerts.good'),
										text: this.$t('GestionExtClaire.alertAcceptInvitation'),
									})
									this.refresh();
								}
              }) 
              .catch(error => {
                if(error.error === "error.unauthorizedAccess"){
                  GlobalMethods.errorToken();
                }
								else if(error.error === "error.already") { 
                    GlobalMethods.errorApi(this.$t("GestionExtClaire.alertBadAccept"));
                    this.refresh();
                  }
                else {
                    GlobalMethods.errorApi(error.error);
                }
              });
				},
				/**
        * Cette méthode permet d'envoyer l'id d'invitation vers l'api afin de refuser une invitation.
        * 
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
        */
				refuse(invitationId){
					const requestOptions = {
						method: "POST",
						headers: {'Authorization': this.$cookies.get("token")}
					};
					fetch(`https://babawallet.alwaysdata.net/api/client/invitedWallets/refuseInvitation/${invitationId}`, requestOptions)
						.then(response => {
							if(!response.ok){
								return response.json().then(json => Promise.reject(json));
							}
							else{
								Swal.fire({
										icon: 'success',
										title: this.$t('alerts.good'),
										text: this.$t('GestionExtClaire.alertRefuseInvitation'),
									})
									this.refresh();
							}
						}) 
						.catch(error => {
							if(error.error === "error.unauthorizedAccess")
                  GlobalMethods.errorToken();
              else
                GlobalMethods.errorApi(error.error);
						});
				},
				/**
        * Cette méthode permet d'envoyer l'id d'invitation vers l'api afin de la supprimer quand elle est lue.
        * 
        * @throws une erreur potentiellement renvoyée par l'API ou une erreur de token gérée dans GlobalMethods.
        */
				seen(invitationId){
					const requestOptions = {
						method: "DELETE",
						headers: {'Authorization': this.$cookies.get("token")}
					};
					fetch(`https://babawallet.alwaysdata.net/api/client/invitedWallets/invitations/${invitationId}`, requestOptions)
						.then(response => {
							if(!response.ok){
                return response.json().then(json => Promise.reject(json));
							}
							else{
								this.refresh();
							}
						}) 
						.catch(error => {
              if(error.error === "error.unauthorizedAccess")
                GlobalMethods.errorToken();
              else
                GlobalMethods.errorApi(error.error);
						});
				}
      }
    }
    </script>
    
    <style scoped>
    
    .header {
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 9999; 
    }
    
    .main {
      display: flex;
      flex-direction: column;
      justify-content: space-evenly;
      align-items: center;
			margin: 10vh;
    }
    
    .bottombutton {
      display: flex;
      justify-content: center;
    }
		.notifs {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: space-evenly;
		}
		
		.notif {
			border-radius: 50px;
			background: #e0e0e0;
			box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
			margin: 10px;
			width: 400px;
		}
    </style>
    
  