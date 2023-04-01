<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="proposal.proposalName"/>
        </div>
    </div>
</template>

<script>
    
    import MainHeader from '@/components/MainHeader.vue';
    import GoButton from '@/components/GoButton.vue';
    import  Swal  from 'sweetalert2/dist/sweetalert2';
    import GlobalMethods from '@/components/GlobalMethods.vue';

    export default
    {
        components:
        {
            GoButton,
            MainHeader
        },
        data()
        {
            return{
                
                providerId: sessionStorage.getItem("providerId"),
                proposalName: sessionStorage.getItem("proposalName"),
                proposal: []
            }
        },
        mounted()
        {
            if(this.$cookies.get("lang"))
                this.$i18n.locale = this.$cookies.get("lang");
            else
                this.$cookies.set("lang", this.$i18n.locale)
        },
        async created()
        {
            const requestOptions = 
            {
                method: "GET",
                headers: {'Authorization': this.$cookies.get("token")}
            };
            try
            {
                const response = await fetch(`https://babawallet.alwaysdata.net/api/client/proposal/${this.providerId}/${this.proposalName}`, requestOptions);
                if(!response.ok)
                {
                    const data = await response.text();
                    if(response.status == 401 && data.trim() === '')
                        throw new Error("Token");
                    else
                    {
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                }
                else
                {
                    const data = await response.json();
                    this.proposal = data.proposal;
                }
            }
            catch(error)
            {
                if(error.message === "Token")
                {
                    this.$cookies.remove("token");
                    this.$cookies.remove(role);
                    Swal.fire('Your connection has expired');
                    this.$router.push("/")
                }
                else
                    GlobalMethods.errorApi(error.message);
            }
        },
        methods:
        {
            makeContractRequest()
            {
                const requestOptions = 
                {
                    method: "POST",
                    headers: {'Authorization': this.$cookies.get("token")},
                    body: JSON.stringify({ nameProposal: this.proposalName, idProvider: this.providerId, ean: this.ean, address: this.address})
                };
                fetch("https://babawallet.alwaysdata.net/api/client/proposeContract/", requestOptions)
                    .then(response => 
                    {
                        if(!response.ok)
                        {
                            const data = response.text();
                            if(response.status == 401 && data.trim() === '')
                                throw new Error("Token");
                            else
                                return response.json().then(json => Promise.reject(json));
                        }
                        else
                        {
                            Swal.fire
                            ({
                                icon: "success",
                                title: 'Good !',
                                text: 'A message has been sent to the provider !'
                            })
                            this.$router.push({name: 'NewContractsPage'});
                        }
                    })
                    .catch(error =>
                    {
                        if(error.message === "Token")
                        {
                            this.$cookies.remove("token");
                            this.$cookies.remove("role");
                            Swal.fire('Your connection has expired');
                            this.$router.push("/");
                        }
                        else
                            GlobalMethods.errorApi(error.error);
                    });
            }
        }
    }
</script>