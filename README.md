# SSG
Social Stream Generation

Due to various reasons such as privacy, bandwith, API barrier imposed by social network service providers,et al, accessing sheer volume of social stream data in real world is often infeasible to researchers. Hence, we develope a data generator that generates data conforming to properties existed in three kinds of real social stream data, including tweet stream crawled from Sina Weibo, patent stream originally released by NBER, and Email stream collected by CALO Project. A local generator and a distributed data generator is currently provided under the fold SSG.
SocialStreamGen is a single node generator. In order to generate massive dataset more efficiently, we also develop a distributed generator, that is ParallelSocialStreamGen under SSG, utilizing the power of commodity machines to conduct some tasks in parallel.
